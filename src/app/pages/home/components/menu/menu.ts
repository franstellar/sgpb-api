import { Component, EventEmitter, Output } from '@angular/core';
import {
  CommonModule,
  registerLocaleData
} from '@angular/common';

import localePt from '@angular/common/locales/pt';

registerLocaleData(localePt, 'pt-BR');

interface ProdutoMenu {
  id: number;
  nome: string;
  descricao: string;
  ingredientes: string[];
  porcao: string;
  preco: number;
  imagem: string;
  etiqueta: string;
}

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './menu.html',
  styleUrl: './menu.scss'
})
export class Menu {

  @Output() adicionarProduto = new EventEmitter<ProdutoMenu>();
  @Output() solicitarDoacao = new EventEmitter<void>();
  @Output() apoiarCampanha = new EventEmitter<void>();
  @Output() montarPedidoEvent = new EventEmitter<void>();

  apoiando = false;

  produtos: ProdutoMenu[] = [
    {
      id: 1,
      nome: 'Paella de frutos do mar',
      descricao: 'Camarão, lula e mexilhão.',
      porcao: 'Individual · 450g',
      preco: 50,
      imagem: '/images/paella-marinera.png',
      etiqueta: 'Mais pedida',
      ingredientes: [
        'Arroz temperado com açafrão',
        'Camarão',
        'Lula em anéis',
        'Mexilhão',
        'Pimentão vermelho',
        'Pimentão amarelo',
        'Ervilhas',
        'Tomate',
        'Cebola',
        'Alho',
        'Azeite de oliva',
        'Caldo de peixe',
        'Limão',
        'Salsinha',
        'Temperos selecionados'
      ]
    },
    {
      id: 4,
      nome: 'Paella de carnes',
      descricao: 'Frango, lombo suíno e linguiça.',
      porcao: 'Individual · 450g',
      preco: 45,
      imagem: '/images/paella-valenciana.png',
      etiqueta: 'Mais pedida',
      ingredientes: [
        'Arroz temperado com açafrão',
        'Frango em pedaços',
        'Lombo suíno',
        'Linguiça',
        'Vagem',
        'Pimentão vermelho',
        'Pimentão amarelo',
        'Ervilhas',
        'Tomate',
        'Cebola',
        'Alho',
        'Azeite de oliva',
        'Caldo especial',
        'Limão',
        'Salsinha',
        'Temperos selecionados'
      ]
    },
    {
      id: 7,
      nome: 'Paella veggie',
      descricao: 'Cogumelos frescos, alcachofras e pimentões.',
      porcao: 'Individual · 450g',
      preco: 40,
      imagem: '/images/paella-vegetariana.png',
      etiqueta: 'Sem carne',
      ingredientes: [
        'Arroz temperado com açafrão',
        'Cogumelos frescos',
        'Alcachofras',
        'Pimentão vermelho',
        'Pimentão amarelo',
        'Ervilhas',
        'Tomate',
        'Azeitonas',
        'Cebola',
        'Alho',
        'Azeite de oliva',
        'Caldo de legumes',
        'Salsinha',
        'Temperos selecionados'
      ]
    },
    {
      id: 3,
      nome: 'Paella frutos do mar (4 pessoas)',
      descricao: 'Camarão, lula e mexilhão para compartilhar.',
      porcao: 'Serve 4 pessoas · 1,8kg',
      preco: 180,
      imagem: '/images/paella-solidaria.png',
      etiqueta: 'Para compartilhar',
      ingredientes: [
        'Arroz temperado com açafrão',
        'Camarão',
        'Lula em anéis',
        'Mexilhão',
        'Pimentão vermelho',
        'Pimentão amarelo',
        'Ervilhas',
        'Tomate',
        'Cebola',
        'Alho',
        'Azeite de oliva',
        'Caldo especial',
        'Limão',
        'Salsinha',
        'Temperos selecionados'
      ]
    }
  ];

  metaTotal = 40000;
  valorArrecadado = 30295;

  get valorRestante(): number {
    return Math.max(0, this.metaTotal - this.valorArrecadado);
  }

  get percentualArrecadado(): number {
    return Math.min(
      100,
      (this.valorArrecadado / this.metaTotal) * 100
    );
  }

  selecionarProduto(produto: ProdutoMenu): void {
    this.adicionarProduto.emit(produto);
  }

  doarMarmita(): void {
    this.solicitarDoacao.emit();
  }

  apoiarMeta(): void {
    this.apoiando = !this.apoiando;
    this.apoiarCampanha.emit();
  }

  montarPedido(): void {
    this.montarPedidoEvent.emit();
  }
}
