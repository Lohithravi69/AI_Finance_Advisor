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
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;

    @Transactional
    public PortfolioResponse createPortfolio(Long userId, PortfolioRequest request) {
        User user = userRepository.findById(userId)
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

        portfolio = portfolioRepository.save(portfolio);
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
        return portfolioRepository.findById(id)
            .map(this::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found"));
    }

    @Transactional
    public PortfolioResponse updatePortfolio(Long id, PortfolioRequest request) {
        Portfolio portfolio = portfolioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found"));

        portfolio.setPortfolioName(request.portfolioName());
        portfolio.setDescription(request.description());
        portfolio.setAllocationStocks(request.allocationStocks());
        portfolio.setAllocationBonds(request.allocationBonds());
        portfolio.setAllocationCrypto(request.allocationCrypto());
        portfolio.setAllocationOther(request.allocationOther());

        portfolio = portfolioRepository.save(portfolio);
        return toResponse(portfolio);
    }

    @Transactional
    public void deletePortfolio(Long id) {
        portfolioRepository.deleteById(id);
    }

    @Transactional
    public PortfolioResponse rebalancePortfolio(Long id) {
        Portfolio portfolio = portfolioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found"));

        portfolio.setRebalanceNeeded(false);
        portfolio = portfolioRepository.save(portfolio);
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
