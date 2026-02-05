package com.aifa.finance.service;

import com.aifa.finance.domain.Investment;
import com.aifa.finance.domain.User;
import com.aifa.finance.dto.InvestmentRequest;
import com.aifa.finance.dto.InvestmentResponse;
import com.aifa.finance.exception.ResourceNotFoundException;
import com.aifa.finance.repository.InvestmentRepository;
import com.aifa.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvestmentService {

    private final InvestmentRepository investmentRepository;
    private final UserRepository userRepository;

    @Transactional
    public InvestmentResponse createInvestment(Long userId, InvestmentRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Investment investment = Investment.builder()
            .user(user)
            .account(null) // Will be set by controller if needed
            .symbol(request.symbol())
            .name(request.name())
            .investmentType(Investment.InvestmentType.valueOf(request.investmentType()))
            .quantity(request.quantity())
            .purchasePrice(request.purchasePrice())
            .currentPrice(request.purchasePrice())
            .totalCost(request.quantity().multiply(request.purchasePrice()))
            .currentValue(request.quantity().multiply(request.purchasePrice()))
            .gainLoss(BigDecimal.ZERO)
            .gainLossPercentage(BigDecimal.ZERO)
            .purchaseDate(request.purchaseDate())
            .lastUpdated(LocalDateTime.now())
            .currency(request.currency() != null ? request.currency() : "USD")
            .notes(request.notes())
            .build();

        investment = investmentRepository.save(investment);
        return toResponse(investment);
    }

    @Transactional(readOnly = true)
    public List<InvestmentResponse> getInvestmentsByUser(Long userId) {
        return investmentRepository.findByUserIdOrderByPurchaseDateDesc(userId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InvestmentResponse> getInvestmentsByAccount(Long userId, Long accountId) {
        return investmentRepository.findByUserIdAndAccountId(userId, accountId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InvestmentResponse> getInvestmentsByType(Long userId, String investmentType) {
        return investmentRepository.findByUserIdAndInvestmentType(userId, investmentType)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InvestmentResponse getInvestmentById(Long id) {
        return investmentRepository.findById(id)
            .map(this::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Investment not found"));
    }

    @Transactional
    public InvestmentResponse updateInvestment(Long id, InvestmentRequest request) {
        Investment investment = investmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Investment not found"));

        investment.setSymbol(request.symbol());
        investment.setName(request.name());
        investment.setInvestmentType(Investment.InvestmentType.valueOf(request.investmentType()));
        investment.setQuantity(request.quantity());
        investment.setPurchasePrice(request.purchasePrice());
        investment.setTotalCost(request.quantity().multiply(request.purchasePrice()));
        investment.setPurchaseDate(request.purchaseDate());
        investment.setCurrency(request.currency() != null ? request.currency() : "USD");
        investment.setNotes(request.notes());
        investment.setLastUpdated(LocalDateTime.now());

        investment = investmentRepository.save(investment);
        return toResponse(investment);
    }

    @Transactional
    public void deleteInvestment(Long id) {
        investmentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalInvested(Long userId) {
        BigDecimal total = investmentRepository.sumTotalCostByUserId(userId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public BigDecimal getCurrentPortfolioValue(Long userId) {
        BigDecimal value = investmentRepository.sumCurrentValueByUserId(userId);
        return value != null ? value : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalGainLoss(Long userId) {
        BigDecimal gainLoss = investmentRepository.sumGainLossByUserId(userId);
        return gainLoss != null ? gainLoss : BigDecimal.ZERO;
    }

    @Transactional
    public InvestmentResponse updatePrice(Long id, BigDecimal newPrice) {
        Investment investment = investmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Investment not found"));

        investment.setCurrentPrice(newPrice);
        investment.setLastUpdated(LocalDateTime.now());
        investment.calculateMetrics();

        investment = investmentRepository.save(investment);
        return toResponse(investment);
    }

    private InvestmentResponse toResponse(Investment investment) {
        return new InvestmentResponse(
            investment.getId(),
            investment.getSymbol(),
            investment.getName(),
            investment.getInvestmentType().name(),
            investment.getQuantity(),
            investment.getPurchasePrice(),
            investment.getCurrentPrice(),
            investment.getTotalCost(),
            investment.getCurrentValue(),
            investment.getGainLoss(),
            investment.getGainLossPercentage(),
            investment.getPurchaseDate(),
            investment.getLastUpdated(),
            investment.getCurrency(),
            investment.getNotes(),
            investment.getCreatedAt(),
            investment.getUpdatedAt()
        );
    }
}
