
import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface Pergunta {
  titulo: string;
  resposta: string;
}

@Component({
  selector: 'app-faq',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './faq.html',
  styleUrl: './faq.scss'
})
export class Faq {

  @Output() realizarPedidoEvent = new EventEmitter<void>();
  @Output() enviarDuvidaEvent = new EventEmitter<string>();

  perguntaAberta: number | null = 0;

  duvida: string = '';

  perguntas: Pergunta[] = [

    {
      titulo: 'Até quando posso comprar?',
      resposta: 'As vendas ficam abertas de 15/10/2026 a 25/11/2026 ou enquanto houver unidades disponíveis.'
    },

    {
      titulo: 'Quais formas de pagamento são aceitas?',
      resposta: 'As formas de pagamento disponíveis serão apresentadas durante a finalização do pedido no aplicativo.'
    },

    {
      titulo: 'O que acontece se eu não pagar a tempo?',
      resposta: 'Caso o pagamento não seja realizado dentro do prazo informado, o pedido poderá ser cancelado e a unidade disponibilizada novamente.'
    },

    {
      titulo: 'Posso pedir para outra pessoa retirar?',
      resposta: 'Sim. Consulte as orientações de retirada da edição e informe os dados necessários da pessoa responsável pela retirada.'
    },

    {
      titulo: 'Posso cancelar meu pedido?',
      resposta: 'Para solicitar o cancelamento, entre em contato com a equipe da organização pelos canais de atendimento.'
    },

    {
      titulo: 'Há opção sem frutos do mar ou sem carne?',
      resposta: 'Sim! Temos opções de paella vegetariana e outras variedades. Confira os ingredientes disponíveis na seção do cardápio.'
    },

    {
      titulo: 'Como recebo a confirmação do meu pedido?',
      resposta: 'Após finalizar a compra e confirmar o pagamento, você poderá consultar as informações e a confirmação do pedido pelo aplicativo.'
    },

    {
      titulo: 'Onde posso acompanhar as informações sobre a retirada?',
      resposta: 'As informações sobre local, data e horário de retirada serão disponibilizadas pelos canais oficiais da campanha.'
    }

  ];

  alternarPergunta(index: number): void {
    this.perguntaAberta =
      this.perguntaAberta === index ? null : index;
  }

  enviarDuvida(): void {
    const mensagem = this.duvida.trim();

    if (!mensagem) {
      return;
    }

    this.enviarDuvidaEvent.emit(mensagem);
    this.duvida = '';
  }

  realizarPedido(): void {
    this.realizarPedidoEvent.emit();
  }

}
