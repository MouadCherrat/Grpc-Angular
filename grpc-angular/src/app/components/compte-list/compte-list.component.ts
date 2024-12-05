import { Component, OnInit } from '@angular/core';
import { CompteService } from '../../services/compte.service';
import { Compte } from '../../grpc/compte_pb';

@Component({
  selector: 'app-compte-list',
  templateUrl: './compte-list.component.html',
  styleUrls: ['./compte-list.component.css']
})
export class CompteListComponent implements OnInit {
  comptes: Compte[] = [];

  constructor(private compteService: CompteService) {}

  ngOnInit(): void {
    this.compteService.getAllComptes()
      .then(response => {
        this.comptes = response.getComptesList();
      })
      .catch(error => {
        console.error('Error fetching comptes:', error);
      });
  }
}
