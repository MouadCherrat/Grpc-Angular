package ma.projet.grpc.controllers;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import ma.projet.grpc.entity.Compte;
import ma.projet.grpc.entity.TypeCompte;
import ma.projet.grpc.repository.CompteRepository;
import ma.projet.grpc.stubs.*;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class CompteServiceImpl extends CompteServiceGrpc.CompteServiceImplBase {

    private final CompteRepository compteRepository;

    @Override
    public void allComptes(GetAllComptesRequest request, StreamObserver<GetAllComptesResponse> responseObserver) {
        List<Compte> comptes = compteRepository.findAll();
        GetAllComptesResponse.Builder responseBuilder = GetAllComptesResponse.newBuilder();
        comptes.forEach(compte -> responseBuilder.addComptes(mapToGrpcCompte(compte)));
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void compteById(GetCompteByIdRequest request, StreamObserver<GetCompteByIdResponse> responseObserver) {
        Optional<Compte> compteOpt = compteRepository.findById(request.getId());
        if (compteOpt.isPresent()) {
            responseObserver.onNext(GetCompteByIdResponse.newBuilder()
                    .setCompte(mapToGrpcCompte(compteOpt.get()))
                    .build());
        } else {
            responseObserver.onError(io.grpc.Status.NOT_FOUND
                    .withDescription("Compte not found for ID: " + request.getId())
                    .asRuntimeException());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void totalSolde(GetTotalSoldeRequest request, StreamObserver<GetTotalSoldeResponse> responseObserver) {
        List<Compte> comptes = compteRepository.findAll();
        int count = comptes.size();
        float sum = (float) comptes.stream().mapToDouble(Compte::getSolde).sum();
        float average = count > 0 ? sum / count : 0;

        SoldeStats stats = SoldeStats.newBuilder()
                .setCount(count)
                .setSum(sum)
                .setAverage(average)
                .build();

        responseObserver.onNext(GetTotalSoldeResponse.newBuilder().setStats(stats).build());
        responseObserver.onCompleted();
    }

    @Override
    public void saveCompte(SaveCompteRequest request, StreamObserver<SaveCompteResponse> responseObserver) {
        CompteRequest compteReq = request.getCompte();
        String id = UUID.randomUUID().toString();

        Compte compte = Compte.builder()
                .id(id)
                .solde(compteReq.getSolde())
                .dateCreation(compteReq.getDateCreation())
                .type(TypeCompte.valueOf(compteReq.getType().name()))
                .build();

        Compte savedCompte = compteRepository.save(compte);

        responseObserver.onNext(SaveCompteResponse.newBuilder().setCompte(mapToGrpcCompte(savedCompte)).build());
        responseObserver.onCompleted();
    }

    @Override
    public void findByType(FindByTypeRequest request, StreamObserver<FindByTypeResponse> responseObserver) {
        TypeCompte type = TypeCompte.valueOf(request.getType().name());
        List<Compte> comptes = compteRepository.findByType(type);

        FindByTypeResponse.Builder responseBuilder = FindByTypeResponse.newBuilder();
        comptes.forEach(compte -> responseBuilder.addComptes(mapToGrpcCompte(compte)));

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void delete(DeleteRequest request, StreamObserver<DeleteResponse> responseObserver) {
        String id = request.getId();
        Optional<Compte> compteOpt = compteRepository.findById(id);

        if (compteOpt.isPresent()) {
            compteRepository.deleteById(id);
            responseObserver.onNext(DeleteResponse.newBuilder().setSuccess(true).build());
        } else {
            responseObserver.onError(io.grpc.Status.NOT_FOUND
                    .withDescription("Compte not found for ID: " + id)
                    .asRuntimeException());
        }

        responseObserver.onCompleted();
    }

    private ma.projet.grpc.stubs.Compte mapToGrpcCompte(Compte compte) {
        return ma.projet.grpc.stubs.Compte.newBuilder()
                .setId(compte.getId())
                .setSolde(compte.getSolde())
                .setDateCreation(compte.getDateCreation())
                .setType(ma.projet.grpc.stubs.TypeCompte.valueOf(compte.getType().name()))
                .build();
    }
}
