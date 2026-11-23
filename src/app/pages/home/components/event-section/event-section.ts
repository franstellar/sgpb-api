
import {
  AfterViewInit,
  Component,
  ElementRef,
  OnDestroy
} from '@angular/core';

@Component({
  selector: 'app-event-section',
  standalone: true,
  imports: [],
  templateUrl: './event-section.html',
  styleUrl: './event-section.scss'
})
export class EventSection implements AfterViewInit, OnDestroy {
  private observer?: IntersectionObserver;

  constructor(private elementRef: ElementRef<HTMLElement>) {}

  ngAfterViewInit(): void {
    const host = this.elementRef.nativeElement;
    const page = host.querySelector<HTMLElement>('.event-page') ?? host;
    const elements = page.querySelectorAll<HTMLElement>('[data-reveal]');

    if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
      elements.forEach(element => element.classList.add('is-visible'));
      return;
    }

    if (typeof IntersectionObserver === 'undefined') {
      elements.forEach(element => element.classList.add('is-visible'));
      return;
    }

    this.observer = new IntersectionObserver(
      entries => {
        entries.forEach(entry => {
          if (entry.isIntersecting) {
            (entry.target as HTMLElement).classList.add('is-visible');
            this.observer?.unobserve(entry.target);
          }
        });
      },
      {
        threshold: 0.12,
        rootMargin: '0px 0px -35px 0px'
      }
    );

    elements.forEach(element => this.observer?.observe(element));
    page.classList.add('reveal-ready');
  }

  ngOnDestroy(): void {
    this.observer?.disconnect();
  }
}
