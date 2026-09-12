import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { App } from './app';

const RESPONSE = [
  {
    id: 'w1',
    employee: 'Steffen Jacobs',
    origin: { name: 'Germany', isoCode: 'DE' },
    destination: { name: 'United States', isoCode: 'US' },
    start: '2024-01-02',
    end: '2024-12-31',
    workingDays: 65,
    risk: 'HIGH_RISK',
  },
];

describe('App', () => {
  let http: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    http = TestBed.inject(HttpTestingController);
  });

  it('renders the workations returned by the API', async () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    await Promise.resolve();

    http.expectOne('http://localhost:8080/workflex/workation').flush(RESPONSE);
    await fixture.whenStable();

    const text = (fixture.nativeElement as HTMLElement).textContent ?? '';
    expect(text).toContain('Steffen Jacobs');
    expect(text).toContain('02/01/2024');
    expect(text).toContain('High risk');
  });

  it('shows a message when the request fails', async () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    await Promise.resolve();

    http
      .expectOne('http://localhost:8080/workflex/workation')
      .flush('boom', { status: 500, statusText: 'Server Error' });
    await fixture.whenStable();

    const text = (fixture.nativeElement as HTMLElement).textContent ?? '';
    expect(text).toContain('Could not load the workations.');
  });
});
