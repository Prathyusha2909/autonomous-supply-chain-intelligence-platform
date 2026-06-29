export type Shipment = {
  id: string;
  orderNumber: string;
  carrier: string;
  origin: string;
  destination: string;
  status: string;
  currentLocationName?: string;
  plannedEta: string;
  predictedEta?: string;
  riskScore?: number;
  updatedAt: string;
};

export type RiskProfile = {
  shipmentId: string;
  orderNumber?: string;
  carrier?: string;
  origin?: string;
  destination?: string;
  lastLocationName?: string;
  status?: string;
  severity?: string;
  exceptionCode?: string;
  riskScore?: number;
  delayMinutes?: number;
  rootCause?: string;
  recommendation?: string;
  updatedAt?: string;
};

export type Bottleneck = {
  id: string;
  locationName: string;
  exceptionCode: string;
  severity: string;
  shipmentsImpacted: number;
  averageDwellMinutes: number;
};

export type Summary = {
  trackedShipments: number;
  highRiskShipments: number;
  activeBottlenecks: number;
  averageRiskScore: number;
};

export type CopilotAnswer = {
  answer: string;
  recommendations: string[];
  evidence: Array<{ type: string; title: string; detail: string }>;
  generatedAt: string;
};

const API_BASE = import.meta.env.VITE_API_BASE_URL ?? '';

async function fetchJson<T>(path: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options
  });
  if (!response.ok) {
    throw new Error(`Request failed: ${response.status}`);
  }
  return response.json() as Promise<T>;
}

export const api = {
  shipments: () => fetchJson<Shipment[]>('/api/shipments'),
  summary: () => fetchJson<Summary>('/api/intelligence/summary'),
  risks: () => fetchJson<RiskProfile[]>('/api/intelligence/risks?threshold=0'),
  bottlenecks: () => fetchJson<Bottleneck[]>('/api/intelligence/bottlenecks'),
  askCopilot: (question: string) =>
    fetchJson<CopilotAnswer>('/api/copilot/query', {
      method: 'POST',
      body: JSON.stringify({ question })
    })
};

export const demoData = {
  shipments: [
    {
      id: 'SHP-SIM-001',
      orderNumber: 'PO-90001',
      carrier: 'Maersk',
      origin: 'Shanghai, CN',
      destination: 'Los Angeles, US',
      status: 'EXCEPTION',
      currentLocationName: 'Port of Singapore',
      plannedEta: '2026-07-05T16:00:00Z',
      predictedEta: '2026-07-06T03:30:00Z',
      riskScore: 0.82,
      updatedAt: new Date().toISOString()
    },
    {
      id: 'SHP-SIM-002',
      orderNumber: 'PO-90002',
      carrier: 'DHL',
      origin: 'Hamburg, DE',
      destination: 'Chicago, US',
      status: 'IN_TRANSIT',
      currentLocationName: 'Rotterdam Port',
      plannedEta: '2026-07-03T10:00:00Z',
      predictedEta: '2026-07-03T13:15:00Z',
      riskScore: 0.38,
      updatedAt: new Date().toISOString()
    },
    {
      id: 'SHP-SIM-003',
      orderNumber: 'PO-90003',
      carrier: 'FedEx',
      origin: 'Mumbai, IN',
      destination: 'Dubai, AE',
      status: 'AT_WAREHOUSE',
      currentLocationName: 'Jebel Ali Port',
      plannedEta: '2026-07-02T22:00:00Z',
      predictedEta: '2026-07-02T22:35:00Z',
      riskScore: 0.19,
      updatedAt: new Date().toISOString()
    }
  ] satisfies Shipment[],
  risks: [
    {
      shipmentId: 'SHP-SIM-001',
      orderNumber: 'PO-90001',
      carrier: 'Maersk',
      origin: 'Shanghai, CN',
      destination: 'Los Angeles, US',
      lastLocationName: 'Port of Singapore',
      status: 'EXCEPTION',
      severity: 'CRITICAL',
      exceptionCode: 'PORT_CONGESTION',
      riskScore: 0.82,
      delayMinutes: 690,
      rootCause: 'Port congestion is increasing dwell time and berth uncertainty.',
      recommendation: 'Escalate to control tower, notify customer success, and book an alternate recovery option.',
      updatedAt: new Date().toISOString()
    }
  ] satisfies RiskProfile[],
  bottlenecks: [
    {
      id: 'demo-bn-1',
      locationName: 'Port of Singapore',
      exceptionCode: 'PORT_CONGESTION',
      severity: 'CRITICAL',
      shipmentsImpacted: 6,
      averageDwellMinutes: 430
    }
  ] satisfies Bottleneck[],
  summary: {
    trackedShipments: 3,
    highRiskShipments: 1,
    activeBottlenecks: 1,
    averageRiskScore: 0.46
  } satisfies Summary
};
