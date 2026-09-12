import { RiskLevel, Workation } from '../../models/workation';

export type SortColumn =
  | 'employee'
  | 'origin'
  | 'destination'
  | 'start'
  | 'end'
  | 'workingDays'
  | 'risk';

export type SortDirection = 'asc' | 'desc';

export interface Sort {
  column: SortColumn;
  direction: SortDirection;
}

/** Business order, not alphabetical: adding a MEDIUM_RISK later must not reshuffle the rest. */
const RISK_RANK: Record<RiskLevel, number> = {
  NO_RISK: 1,
  LOW_RISK: 2,
  HIGH_RISK: 3,
};

function compare(a: Workation, b: Workation, column: SortColumn): number {
  switch (column) {
    case 'employee':
      return a.employee.localeCompare(b.employee);
    case 'origin':
      return a.origin.name.localeCompare(b.origin.name);
    case 'destination':
      return a.destination.name.localeCompare(b.destination.name);
    case 'start':
      return a.start.getTime() - b.start.getTime();
    case 'end':
      return a.end.getTime() - b.end.getTime();
    case 'workingDays':
      return a.workingDays - b.workingDays;
    case 'risk':
      return RISK_RANK[a.risk] - RISK_RANK[b.risk];
  }
}

/** Returns a new array: Array.prototype.sort mutates, and the input belongs to the caller. */
export function sortWorkations(workations: readonly Workation[], sort: Sort): Workation[] {
  const factor = sort.direction === 'asc' ? 1 : -1;
  return [...workations].sort((a, b) => factor * compare(a, b, sort.column));
}
