import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RiskLevel } from '../../models/workation';
import { RiskCell } from './risk-cell';

describe('RiskCell', () => {
  let fixture: ComponentFixture<RiskCell>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({ imports: [RiskCell] }).compileComponents();
    fixture = TestBed.createComponent(RiskCell);
  });

  function render(risk: RiskLevel) {
    fixture.componentRef.setInput('risk', risk);
    fixture.detectChanges();
    const element = fixture.nativeElement as HTMLElement;
    return {
      label: element.textContent?.trim() ?? '',
      icon: element.querySelector('img')?.getAttribute('src') ?? '',
    };
  }

  it('labels a high risk as such', () => {
    expect(render('HIGH_RISK')).toEqual({ label: 'High risk', icon: 'risk/red-risk.svg' });
  });

  /** The brief is explicit: both are labelled "No risk" and differ only in colour. */
  it('labels both low and no risk as "No risk", with different icons', () => {
    expect(render('LOW_RISK')).toEqual({ label: 'No risk', icon: 'risk/yellow-risk.svg' });
    expect(render('NO_RISK')).toEqual({ label: 'No risk', icon: 'risk/green-risk.svg' });
  });
});
