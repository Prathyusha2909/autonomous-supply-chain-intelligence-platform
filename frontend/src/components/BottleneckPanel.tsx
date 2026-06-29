import { AlertOctagon } from 'lucide-react';
import type { Bottleneck } from '../lib/api';

type Props = {
  bottlenecks: Bottleneck[];
};

export function BottleneckPanel({ bottlenecks }: Props) {
  return (
    <section className="panel bottleneck-panel">
      <div className="panel-heading">
        <div>
          <p>Exceptions</p>
          <h2>Bottlenecks</h2>
        </div>
        <AlertOctagon size={20} aria-hidden="true" />
      </div>
      <div className="bottleneck-list">
        {bottlenecks.map((bottleneck) => (
          <article className="bottleneck" key={bottleneck.id}>
            <div>
              <strong>{bottleneck.locationName}</strong>
              <span>{bottleneck.exceptionCode.replace('_', ' ')}</span>
            </div>
            <div>
              <strong>{bottleneck.shipmentsImpacted}</strong>
              <span>{Math.round(bottleneck.averageDwellMinutes)} min dwell</span>
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}
