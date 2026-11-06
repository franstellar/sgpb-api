export const COLORS = {

  // Paleta oficial Paelito
  tomate: '#D83B2D',       // Botões principais, destaques e logo
  acafrao: '#F4B82A',      // Destaques secundários e status Pendente
  mediterraneo: '#236A91', // Apoio institucional, Admin/Voluntário e status Pago
  creme: '#FFF5E1',        // Fundo principal das telas
  preto: '#242424',        // Textos principais e status Retirado
  branco: '#FFFFFF',       // Cards e superfícies
  cinzaClaro: '#F3E9D2',   // Bordas suaves sobre o fundo creme
  cinzaTexto: '#6E6A64',   // Subtítulos e descrições

  // Cores semânticas
  status: {
    PENDING: '#F4B82A',    // Açafrão
    PAID: '#236A91',       // Mediterrâneo
    REDEEMED: '#242424',   // Preto
    EXPIRED: '#8C857B',    // Cinza
    CANCELED: '#D83B2D',   // Tomate
    REFUNDED: '#6E6A64',   // Cinza escuro
  }
  
};

export const FONTS = {
  titleBold: 'Baloo2_700Bold',
  titleExtraBold: 'Baloo2_800ExtraBold',
  bodyRegular: 'Poppins_400Regular',
  bodyMedium: 'Poppins_500Medium',
  bodySemiBold: 'Poppins_600SemiBold',
  bodyBold: 'Poppins_700Bold',
};

export const SHADOWS = {
  card: {
    shadowColor: '#242424',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.08,
    shadowRadius: 8,
    elevation: 3,
  },
};