import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AboutAbadeus } from './about-abadeus';

describe('AboutAbadeus', () => {
  let component: AboutAbadeus;
  let fixture: ComponentFixture<AboutAbadeus>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AboutAbadeus],
    }).compileComponents();

    fixture = TestBed.createComponent(AboutAbadeus);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
