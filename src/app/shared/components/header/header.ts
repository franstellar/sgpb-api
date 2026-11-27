import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './header.html',
  styleUrl: './header.scss'
})
export class Header {
  modalAppAberto = false;
  appUrl = 'https://expo.dev/accounts/franboises-team/projects/sgpb-app/builds/db99d3e3-a10d-4f46-bac8-b3fe3ed9432e';

  get qrCodeUrl(): string {
    return `https://api.qrserver.com/v1/create-qr-code/?size=220x220&data=${encodeURIComponent(this.appUrl)}`;
  }

  abrirModalApp(event: Event): void {
    event.preventDefault();
    this.modalAppAberto = true;
  }

  fecharModalApp(): void {
    this.modalAppAberto = false;
  }
}
