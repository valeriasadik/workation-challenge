import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Workation } from '../../models/workation';
import { WorkationsTable } from './workations-table';

function workation(id: string, employee: string): Workation {
  return {
    id,
    employee,
    origin: { name: 'Germany', isoCode: 'DE' },
    destination: { name: 'Spain', isoCode: 'ES' },
    start: new Date('2024-01-02'),
    end: new Date('2024-01-03'),
    workingDays: 1,
    risk: 'NO_RISK',
  };
}

const ROWS = [workation('c', 'Carla'), workation('a', 'Ana'), workation('b', 'Bruno')];

describe('WorkationsTable', () => {
  let fixture: ComponentFixture<WorkationsTable>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({ imports: [WorkationsTable] }).compileComponents();

    fixture = TestBed.createComponent(WorkationsTable);
    fixture.componentRef.setInput('workations', ROWS);
    fixture.detectChanges();
  });

  function employees(): string[] {
    return Array.from(
      (fixture.nativeElement as HTMLElement).querySelectorAll('tbody tr td:first-child'),
    ).map((cell) => cell.textContent?.trim() ?? '');
  }

  function clickHeader(label: string): void {
    const button = Array.from(
      (fixture.nativeElement as HTMLElement).querySelectorAll<HTMLButtonElement>('thead button'),
    ).find((candidate) => candidate.textContent?.includes(label));
    button?.click();
    fixture.detectChanges();
  }

  it('keeps the order it was given until the user sorts', () => {
    expect(employees()).toEqual(['Carla', 'Ana', 'Bruno']);
  });

  it('sorts ascending on the first click', () => {
    clickHeader('Employee');

    expect(employees()).toEqual(['Ana', 'Bruno', 'Carla']);
  });

  it('reverses when the same column is clicked again', () => {
    clickHeader('Employee');
    clickHeader('Employee');

    expect(employees()).toEqual(['Carla', 'Bruno', 'Ana']);
  });

  it('starts ascending again when another column is clicked', () => {
    clickHeader('Employee');
    clickHeader('Employee');
    clickHeader('Working days');
    clickHeader('Employee');

    expect(employees()).toEqual(['Ana', 'Bruno', 'Carla']);
  });

  it('announces the sorted column to assistive technology', () => {
    clickHeader('Employee');

    const headers = (fixture.nativeElement as HTMLElement).querySelectorAll('thead th');
    expect(headers[0].getAttribute('aria-sort')).toBe('ascending');
    expect(headers[1].getAttribute('aria-sort')).toBe('none');
  });
});
