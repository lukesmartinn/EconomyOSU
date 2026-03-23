package cz.osu.invoice_system.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "partners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Partner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // Jméno a příjmení nebo název firmy

    @Column(unique = true, nullable = false)
    private String ico; // IČO (Identifikační číslo osoby)

    private String dic; // DIČ (Daňové identifikační číslo)

    private String street; // Ulice a číslo popisné
    
    private String city; // Město
    
    private String zipCode; // PSČ
    
    private String country; // Stát (např. CZ)
    
    private String email;
    
    private String phone;
}
