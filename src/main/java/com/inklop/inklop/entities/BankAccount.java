package com.inklop.inklop.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.inklop.inklop.entities.valueObject.Status;
import com.inklop.inklop.entities.valueObject.campaign.Currency;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bank_account")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //nombre del banco
    @Column(name="bank_name")
    private String bankName;

    @Column(name="account_holder_name")
    private String accountHolderName;

    @Column(name="dni")
    private String dni;

    //numero de cuenta
    @Column(name="bank_number")
    private String accountNumber;

    //numero de cuenta interbancaria
    @Column(name="interbank_number")
    private String interbankNumber;

    //tipo de cuenta (ahorros, sueldo, etc)
    @Column(name="account_type")
    private String accountType;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="user_id",referencedColumnName = "id")
    private User user;

    @JsonIgnore
    @OneToMany(mappedBy = "bankAccount")
    private List<BankWithdrawal> bankWithdrawals;


    @Enumerated(EnumType.STRING)
    private Status status;

    @PrePersist
    void prePersist(){
        this.status=Status.ACTIVE;
    }
    

    
}
