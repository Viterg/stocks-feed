package ru.viterg.proselyte.stocksfeed.companies;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

interface CompaniesRepository extends R2dbcRepository<Company, Integer> {

}
