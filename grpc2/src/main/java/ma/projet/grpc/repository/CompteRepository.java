package ma.projet.grpc.repository;

import ma.projet.grpc.entity.Compte;
import ma.projet.grpc.entity.TypeCompte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompteRepository extends JpaRepository<Compte, String> {
    List<Compte> findByType(TypeCompte type);
}
