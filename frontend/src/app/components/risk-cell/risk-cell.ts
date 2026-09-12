import { Component, computed, input } from '@angular/core';
import { RiskLevel } from '../../models/workation';


const RISK: Record<RiskLevel, { label: string; icon: string }> = {
  HIGH_RISK: { label: 'High risk', icon: 'risk/red-risk.svg' },
  LOW_RISK: { label: 'No risk', icon: 'risk/yellow-risk.svg' },
  NO_RISK: { label: 'No risk', icon: 'risk/green-risk.svg' },
};

@Component({
  selector: 'app-risk-cell',
  templateUrl: './risk-cell.html',
  styleUrl: './risk-cell.scss',
})
export class RiskCell {
  readonly risk = input.required<RiskLevel>();

  protected readonly status = computed(() => RISK[this.risk()]);
}
