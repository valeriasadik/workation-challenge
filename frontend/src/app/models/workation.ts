/**
 * A union of string literals rather than an enum: the values are exactly what the API sends, so
 * nothing needs translating, and the type disappears at compile time instead of emitting an object.
 */
export type RiskLevel = 'HIGH_RISK' | 'LOW_RISK' | 'NO_RISK';

export interface Country {
  name: string;
  isoCode: string;
}

/** A workation as the application uses it: real Dates, so DatePipe and sorting work. */
export interface Workation {
  /** Business identifier from the source file, never the database key. */
  id: string;
  employee: string;
  origin: Country;
  destination: Country;
  start: Date;
  end: Date;
  workingDays: number;
  risk: RiskLevel;
}

/**
 * The same workation as it arrives over HTTP, where dates are ISO strings.
 *
 * <p>Derived from Workation so the two cannot drift apart. Declaring `start: Date` on the response
 * would type-check and then fail at runtime, since TypeScript validates nothing once compiled.
 */
export type WorkationResponse = Omit<Workation, 'start' | 'end'> & {
  start: string;
  end: string;
};
