import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventSection } from './event-section';

describe('EventSection', () => {
  let component: EventSection;
  let fixture: ComponentFixture<EventSection>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EventSection],
    }).compileComponents();

    fixture = TestBed.createComponent(EventSection);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
