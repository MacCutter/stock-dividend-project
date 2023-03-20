package com.example.stockdividend.scraper;

import com.example.stockdividend.model.Company;
import com.example.stockdividend.model.ScrapedResult;

public interface Scraper {
    Company scrapCompanyByTicker(String ticker);

    ScrapedResult scrap(Company company);
}
