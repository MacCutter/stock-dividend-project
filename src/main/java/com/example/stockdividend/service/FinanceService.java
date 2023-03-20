package com.example.stockdividend.service;

import com.example.stockdividend.exception.impl.NoCompanyException;
import com.example.stockdividend.model.Company;
import com.example.stockdividend.model.Dividend;
import com.example.stockdividend.model.ScrapedResult;
import com.example.stockdividend.model.constants.CacheKey;
import com.example.stockdividend.persist.CompanyRepository;
import com.example.stockdividend.persist.DividendRepository;
import com.example.stockdividend.persist.entity.CompanyEntity;
import com.example.stockdividend.persist.entity.DividendEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
    public ScrapedResult getDividendByCompanyName(String companyName) {
        log.info("search company -> " + companyName);

        // 1. 회사명을 기준으로 회사 정보를 조회
        CompanyEntity company = this.companyRepository.findByName(companyName)
                                                .orElseThrow(() -> new NoCompanyException());
                                            // orElseThrow 사용시 findByName의 옵셔널값이 존재하지 않으면 예외처리 or 존재하면 옵셔널을 벗겨서 사용하게 해줌

        // 2. 조회된 회사 ID로 배당금 정보 조회
        List< DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(company.getId());

        // 3. 결과 조합 후 반환 (Entity를 Model Class로 맵핑 해야함)

        // for문 방식
//        List<Dividend> dividends = new ArrayList<>();
//        for (var entity : dividendEntities) {
//            dividends.add(Dividend.builder()
//                                    .date(entity.getDate())
//                                    .dividend(entity.getDividend())
//                                    .build());
//        }

        // stream 방식
        List<Dividend> dividends = dividendEntities.stream()
                                                    .map(e -> new Dividend(e.getDate(), e.getDividend()))
                                                    .collect(Collectors.toList());

        return new ScrapedResult(new Company(company.getTicker(), company.getName()), dividends);

    }
}
