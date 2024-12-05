import { Injectable } from '@angular/core';
import { CompteServiceClient } from '../grpc/CompteServiceClientPb';
import { GetAllComptesRequest, GetAllComptesResponse } from '../grpc/compte_pb';

@Injectable({
  providedIn: 'root'
})
export class CompteService {
  private client: CompteServiceClient;

  constructor() {
    this.client = new CompteServiceClient('http://localhost:8080', null, null); // Envoy proxy address
  }

  getAllComptes(): Promise<GetAllComptesResponse> {
    return new Promise((resolve, reject) => {
      const request = new GetAllComptesRequest();

      this.client.allComptes(request, {}, (err, response) => {
        if (err) {
          reject(err);
        } else {
          resolve(response);
        }
      });
    });
  }
}
