package com.aifa.finance.service;

import com.aifa.finance.domain.Portfolio;
import com.aifa.finance.domain.User;
import com.aifa.finance.dto.PortfolioRequest;
import com.aifa.finance.dto.PortfolioResponse;
import com.aifa.finance.exception.ResourceNotFoundException;
import com.aifa.finance.repository.PortfolioRepository;
import com.aifa.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;

    @Transactional
    public PortfolioResponse createPortfolio(Long userId, PortfolioRequest request) {
        User user = userRepository.findById(Objects.requireNonNull(userId, "userId must not be null"))
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Portfolio portfolio = Portfolio.builder()
            .user(user)
            .account(null)
            .portfolioName(request.portfolioName())
            .description(request.description())
            .totalInvested(BigDecimal.ZERO)
            .currentValue(BigDecimal.ZERO)
            .totalGainLoss(BigDecimal.ZERO)
            .totalReturnPercentage(BigDecimal.ZERO)
            .investmentCount(0)
            .allocationStocks(request.allocationStocks())
            .allocationBonds(request.allocationBonds())
            .allocationCrypto(request.allocationCrypto())
            .allocationOther(request.allocationOther())
            .rebalanceNeeded(false)
            .build();

        portfolio = Objects.requireNonNull(portfolioRepository.save(portfolio), "Saved portfolio must not be null");
        return toResponse(portfolio);
    }

    @Transactional(readOnly = true)
    public List<PortfolioResponse> getPortfoliosByUser(Long userId) {
        return portfolioRepository.findByUserIdOrderByUpdatedAtDesc(userId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PortfolioResponse getPortfolioById(Long id) {
        return portfolioRepository.findById(Objects.requireNonNull(id, "id must not be null"))
            .map(this::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found"));
    }

    @Transactional
    public PortfolioResponse updatePortfolio(Long id, PortfolioRequest request) {
        Long safeId = Objects.requireNonNull(id, "id must not be null");
        Portfolio portfolio = portfolioRepository.findById(safeId)
            .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found"));

        portfolio.setPortfolioName(request.portfolioName());
        portfolio.setDescription(request.description());
        portfolio.setAllocationStocks(request.allocationStocks());
        portfolio.setAllocationBonds(request.allocationBonds());
        portfolio.setAllocationCrypto(request.allocationCrypto());
        portfolio.setAllocationOther(request.allocationOther());

        portfolio = Objects.requireNonNull(portfolioRepository.save(portfolio), "Updated portfolio must not be null");
        return toResponse(portfolio);
    }

    @Transactional
    public void deletePortfolio(Long id) {
        portfolioRepository.deleteById(Objects.requireNonNull(id, "id must not be null"));
    }

    @Transactional
    public PortfolioResponse rebalancePortfolio(Long id) {
        Portfolio portfolio = portfolioRepository.findById(Objects.requireNonNull(id, "id must not be null"))
            .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found"));

        portfolio.setRebalanceNeeded(false);
        portfolio = Objects.requireNonNull(portfolioRepository.save(portfolio), "Rebalanced portfolio must not be null");
        return toResponse(portfolio);
    }

    private PortfolioResponse toResponse(Portfolio portfolio) {
        return new PortfolioResponse(
            portfolio.getId(),
            portfolio.getPortfolioName(),
            portfolio.getDescription(),
            portfolio.getTotalInvested(),
            portfolio.getCurrentValue(),
            portfolio.getTotalGainLoss(),
            portfolio.getTotalReturnPercentage(),
            portfolio.getInvestmentCount(),
            portfolio.getAllocationStocks(),
            portfolio.getAllocationBonds(),
            portfolio.getAllocationCrypto(),
            portfolio.getAllocationOther(),
            portfolio.getRebalanceNeeded(),
            portfolio.getCreatedAt(),
            portfolio.getUpdatedAt()
        );
    }
}
