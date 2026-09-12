import { RiskLevel, Workation } from '../../models/workation';
import { SortColumn, sortWorkations } from './sort-workations';

function workation(overrides: Partial<Workation> = {}): Workation {
  return {
    id: 'w1',
    employee: 'Steffen Jacobs',
    origin: { name: 'Germany', isoCode: 'DE' },
    destination: { name: 'Spain', isoCode: 'ES' },
    start: new Date('2024-01-02'),
    end: new Date('2024-01-03'),
    workingDays: 1,
    risk: 'NO_RISK',
    ...overrides,
  };
}

function sorted(rows: Workation[], column: SortColumn, direction: 'asc' | 'desc' = 'asc') {
  return sortWorkations(rows, { column, direction }).map((row) => row.id);
}

describe('sortWorkations', () => {
  it('orders text the way a person would, not by character code', () => {
    const rows = [
      workation({ id: 'zapata', employee: 'Zapata' }),
      workation({ id: 'alvarez', employee: 'Álvarez' }),
    ];

    expect(sorted(rows, 'employee')).toEqual(['alvarez', 'zapata']);
  });

  it('reverses on descending', () => {
    const rows = [
      workation({ id: 'a', employee: 'Ana' }),
      workation({ id: 'b', employee: 'Bruno' }),
    ];

    expect(sorted(rows, 'employee', 'desc')).toEqual(['b', 'a']);
  });

  it('compares working days as numbers', () => {
    const rows = [
      workation({ id: 'nine', workingDays: 9 }),
      workation({ id: 'many', workingDays: 131 }),
      workation({ id: 'some', workingDays: 65 }),
    ];

    expect(sorted(rows, 'workingDays')).toEqual(['nine', 'some', 'many']);
  });

  it('compares dates chronologically', () => {
    const rows = [
      workation({ id: 'later', start: new Date('2024-06-01') }),
      workation({ id: 'earlier', start: new Date('2022-09-01') }),
    ];

    expect(sorted(rows, 'start')).toEqual(['earlier', 'later']);
  });

  it('orders risk by severity, not alphabetically', () => {
    const rows: Workation[] = (['HIGH_RISK', 'NO_RISK', 'LOW_RISK'] as RiskLevel[]).map((risk) =>
      workation({ id: risk, risk }),
    );

    expect(sorted(rows, 'risk')).toEqual(['NO_RISK', 'LOW_RISK', 'HIGH_RISK']);
  });

  it('keeps the original order of rows that compare equal', () => {
    const rows = ['first', 'second', 'third'].map((id) => workation({ id, risk: 'HIGH_RISK' }));

    expect(sorted(rows, 'risk')).toEqual(['first', 'second', 'third']);
  });

  it('leaves the array it was given untouched', () => {
    const rows = [
      workation({ id: 'b', employee: 'Bruno' }),
      workation({ id: 'a', employee: 'Ana' }),
    ];

    sortWorkations(rows, { column: 'employee', direction: 'asc' });

    expect(rows.map((row) => row.id)).toEqual(['b', 'a']);
  });
});
