import { Activity, AlertTriangle, Boxes, Gauge } from 'lucide-react';
import type { Summary } from '../lib/api';

type Props = {
  summary: Summary;
  live: boolean;
};

export function StatStrip({ summary, live }: Props) {
  const stats = [
    { label: 'Tracked shipments', value: summary.trackedShipments, icon: Boxes },
    { label: 'High risk', value: summary.highRiskShipments, icon: AlertTriangle },
    { label: 'Bottlenecks', value: summary.activeBottlenecks, icon: Activity },
    { label: 'Average risk', value: `${Math.round(summary.averageRiskScore * 100)}%`, icon: Gauge }
  ];

  return (
    <section className="stat-strip" aria-label="Operations summary">
      {stats.map((stat) => {
        const Icon = stat.icon;
        return (
          <div className="stat" key={stat.label}>
            <Icon size={18} aria-hidden="true" />
            <span>{stat.label}</span>
            <strong>{stat.value}</strong>
          </div>
        );
      })}
      <div className={`stream-state ${live ? 'live' : 'demo'}`}>{live ? 'Live stream' : 'Demo data'}</div>
    </section>
  );
}
