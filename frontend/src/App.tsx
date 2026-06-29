import { RefreshCw, ShieldCheck } from 'lucide-react';
import { useEffect, useMemo, useState } from 'react';
import { BottleneckPanel } from './components/BottleneckPanel';
import { CopilotPanel } from './components/CopilotPanel';
import { RiskMap } from './components/RiskMap';
import { ShipmentTable } from './components/ShipmentTable';
import { StatStrip } from './components/StatStrip';
import { api, demoData, type Bottleneck, type CopilotAnswer, type RiskProfile, type Shipment, type Summary } from './lib/api';
import './styles/app.css';

type DashboardState = {
  shipments: Shipment[];
  risks: RiskProfile[];
  bottlenecks: Bottleneck[];
  summary: Summary;
  live: boolean;
};

export default function App() {
  const [dashboard, setDashboard] = useState<DashboardState>({
    shipments: demoData.shipments,
    risks: demoData.risks,
    bottlenecks: demoData.bottlenecks,
    summary: demoData.summary,
    live: false
  });
  const [question, setQuestion] = useState('Which shipments are likely delayed and what should ops do next?');
  const [answer, setAnswer] = useState<CopilotAnswer | undefined>();
  const [loading, setLoading] = useState(false);

  const highRisk = useMemo(
    () => dashboard.risks.filter((risk) => (risk.riskScore ?? 0) >= 0.45),
    [dashboard.risks]
  );

  async function refresh() {
    try {
      const [shipments, risks, bottlenecks, summary] = await Promise.all([
        api.shipments(),
        api.risks(),
        api.bottlenecks(),
        api.summary()
      ]);
      setDashboard({ shipments, risks, bottlenecks, summary, live: true });
    } catch {
      setDashboard({
        shipments: demoData.shipments,
        risks: demoData.risks,
        bottlenecks: demoData.bottlenecks,
        summary: demoData.summary,
        live: false
      });
    }
  }

  async function askCopilot() {
    if (question.trim().length === 0) {
      return;
    }
    setLoading(true);
    try {
      setAnswer(await api.askCopilot(question));
    } catch {
      setAnswer({
        answer: 'The highest-risk shipment is SHP-SIM-001. Port congestion is driving the delay risk, so operations should escalate recovery planning and notify the customer.',
        recommendations: [
          'Ask Maersk for a berth recovery plan.',
          'Prepare an updated ETA notice for PO-90001.',
          'Evaluate alternate routing if the next scan does not improve.'
        ],
        evidence: [
          { type: 'shipment', title: 'SHP-SIM-001 risk 82%', detail: 'Port congestion is increasing dwell time and berth uncertainty.' },
          { type: 'bottleneck', title: 'Port of Singapore', detail: '6 impacted shipments, average dwell 430 minutes' }
        ],
        generatedAt: new Date().toISOString()
      });
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    refresh();
  }, []);

  return (
    <main className="app-shell">
      <header className="topbar">
        <div>
          <div className="brand-row">
            <ShieldCheck size={22} aria-hidden="true" />
            <span>Autonomous Supply Chain Intelligence</span>
          </div>
          <h1>Real-Time Logistics Control Tower</h1>
        </div>
        <button className="icon-button" type="button" onClick={refresh} title="Refresh operations data">
          <RefreshCw size={18} aria-hidden="true" />
        </button>
      </header>

      <StatStrip summary={dashboard.summary} live={dashboard.live} />

      <section className="dashboard-grid" aria-label="Operations dashboard">
        <RiskMap risks={highRisk.length ? highRisk : dashboard.risks} />
        <CopilotPanel
          question={question}
          answer={answer}
          loading={loading}
          onQuestionChange={setQuestion}
          onAsk={askCopilot}
        />
        <ShipmentTable shipments={dashboard.shipments} />
        <BottleneckPanel bottlenecks={dashboard.bottlenecks} />
      </section>
    </main>
  );
}
