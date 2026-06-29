import { MapPinned } from 'lucide-react';
import type { RiskProfile } from '../lib/api';

type Props = {
  risks: RiskProfile[];
};

const points = [
  { name: 'Shanghai', left: '75%', top: '45%' },
  { name: 'Singapore', left: '67%', top: '63%' },
  { name: 'Rotterdam', left: '49%', top: '34%' },
  { name: 'Los Angeles', left: '16%', top: '43%' },
  { name: 'Dubai', left: '58%', top: '52%' }
];

export function RiskMap({ risks }: Props) {
  const topRisk = risks[0];

  return (
    <section className="panel map-panel">
      <div className="panel-heading">
        <div>
          <p>Network</p>
          <h2>Risk Hotspots</h2>
        </div>
        <MapPinned size={20} aria-hidden="true" />
      </div>
      <div className="network-map" role="img" aria-label="Global logistics risk map">
        <div className="route route-one" />
        <div className="route route-two" />
        {points.map((point) => (
          <div className="map-point" key={point.name} style={{ left: point.left, top: point.top }}>
            <span />
            <small>{point.name}</small>
          </div>
        ))}
      </div>
      <div className="insight-line">
        <strong>{topRisk?.lastLocationName ?? 'No hotspot yet'}</strong>
        <span>{topRisk?.rootCause ?? 'Waiting for Kafka events from the simulator or shipment API.'}</span>
      </div>
    </section>
  );
}
