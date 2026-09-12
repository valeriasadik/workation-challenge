import { DatePipe } from '@angular/common';
import { Component, computed, input, signal } from '@angular/core';
import { Workation } from '../../models/workation';
import { CountryCell } from '../country-cell/country-cell';
import { RiskCell } from '../risk-cell/risk-cell';
import { SortIndicator } from '../sort-indicator/sort-indicator';
import { Sort, SortColumn, sortWorkations } from './sort-workations';

const COLUMNS: { key: SortColumn; label: string }[] = [
  { key: 'employee', label: 'Employee' },
  { key: 'origin', label: 'Origin' },
  { key: 'destination', label: 'Destination' },
  { key: 'start', label: 'Start' },
  { key: 'end', label: 'End' },
  { key: 'workingDays', label: 'Working days' },
  { key: 'risk', label: 'Risk' },
];

@Component({
  selector: 'app-workations-table',
  imports: [DatePipe, CountryCell, RiskCell, SortIndicator],
  templateUrl: './workations-table.html',
  styleUrl: './workations-table.scss',
})
export class WorkationsTable {
  readonly workations = input.required<readonly Workation[]>();

  /** null until the user sorts: the rows keep the order the API sent them in. */
  private readonly sort = signal<Sort | null>(null);

  /** The sorted array is not state, it is a consequence: storing it would create a second truth. */
  protected readonly rows = computed(() => {
    const sort = this.sort();
    return sort ? sortWorkations(this.workations(), sort) : [...this.workations()];
  });

  /** Resolved here so the template calls no methods while rendering. */
  protected readonly headers = computed(() => {
    const sort = this.sort();
    return COLUMNS.map((header) => {
      const direction = sort?.column === header.key ? sort.direction : null;
      return {
        ...header,
        active: direction !== null,
        descending: direction === 'desc',
        ariaSort: direction === null ? 'none' : direction === 'asc' ? 'ascending' : 'descending',
      };
    });
  });

  protected sortBy(column: SortColumn): void {
    this.sort.update((current) =>
      current?.column === column
        ? { column, direction: current.direction === 'asc' ? 'desc' : 'asc' }
        : { column, direction: 'asc' },
    );
  }
}
