package ma.projet.grpc.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Compte {
    @Id
    private String id;
    private float solde;
    private String dateCreation;

    @Enumerated(EnumType.STRING)
    private TypeCompte type;
}
