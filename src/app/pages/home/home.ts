import { Component } from '@angular/core';

import { Hero } from './components/hero/hero';
import { HowItWorks } from './components/how-it-works/how-it-works';
import { EventSection } from './components/event-section/event-section';
import { AboutAbadeus } from './components/about-abadeus/about-abadeus';
import { Menu } from './components/menu/menu';
import { Faq } from './components/faq/faq';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    Hero,
    HowItWorks,
    EventSection,
    AboutAbadeus,
    Menu,
    Faq,
  ],
  templateUrl: './home.html',
  styleUrl: './home.scss'
})
export class Home {}