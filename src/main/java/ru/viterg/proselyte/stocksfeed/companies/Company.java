package ru.viterg.proselyte.stocksfeed.companies;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "companies", schema = "public")
public class Company {

    @Id
    @Column("id")
    private Integer id;

    @Column("ticker")
    private String ticker;

}
