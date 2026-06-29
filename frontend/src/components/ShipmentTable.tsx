import { ArrowUpRight, Clock } from 'lucide-react';
import type { Shipment } from '../lib/api';

type Props = {
  shipments: Shipment[];
};

export function ShipmentTable({ shipments }: Props) {
  return (
    <section className="panel table-panel">
      <div className="panel-heading">
        <div>
          <p>Visibility</p>
          <h2>Active Shipments</h2>
        </div>
        <Clock size={20} aria-hidden="true" />
      </div>
      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Shipment</th>
              <th>Lane</th>
              <th>Location</th>
              <th>Status</th>
              <th>Risk</th>
            </tr>
          </thead>
          <tbody>
            {shipments.map((shipment) => (
              <tr key={shipment.id}>
                <td>
                  <strong>{shipment.id}</strong>
                  <span>{shipment.carrier} | {shipment.orderNumber}</span>
                </td>
                <td>
                  <span>{shipment.origin}</span>
                  <ArrowUpRight size={14} aria-hidden="true" />
                  <span>{shipment.destination}</span>
                </td>
                <td>{shipment.currentLocationName ?? 'Awaiting scan'}</td>
                <td><span className={`status ${shipment.status.toLowerCase()}`}>{shipment.status.replace('_', ' ')}</span></td>
                <td><RiskBar value={shipment.riskScore ?? 0} /></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}

function RiskBar({ value }: { value: number }) {
  return (
    <div className="risk-cell">
      <div className="risk-track">
        <span style={{ width: `${Math.round(value * 100)}%` }} />
      </div>
      <strong>{Math.round(value * 100)}%</strong>
    </div>
  );
}
