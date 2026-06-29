import { Bot, Send } from 'lucide-react';
import type { CopilotAnswer } from '../lib/api';

type Props = {
  question: string;
  answer?: CopilotAnswer;
  loading: boolean;
  onQuestionChange: (question: string) => void;
  onAsk: () => void;
};

export function CopilotPanel({ question, answer, loading, onQuestionChange, onAsk }: Props) {
  return (
    <section className="panel copilot-panel">
      <div className="panel-heading">
        <div>
          <p>AI Operations</p>
          <h2>Copilot</h2>
        </div>
        <Bot size={20} aria-hidden="true" />
      </div>
      <div className="copilot-answer">
        <p>{answer?.answer ?? 'Ask about delays, bottlenecks, or root cause across the logistics network.'}</p>
        {answer?.recommendations?.length ? (
          <ul>
            {answer.recommendations.map((item) => <li key={item}>{item}</li>)}
          </ul>
        ) : null}
      </div>
      <div className="evidence-list">
        {answer?.evidence?.slice(0, 3).map((item) => (
          <div className="evidence" key={`${item.type}-${item.title}`}>
            <strong>{item.title}</strong>
            <span>{item.detail}</span>
          </div>
        ))}
      </div>
      <div className="copilot-input">
        <input
          value={question}
          onChange={(event) => onQuestionChange(event.target.value)}
          onKeyDown={(event) => {
            if (event.key === 'Enter') {
              onAsk();
            }
          }}
          aria-label="Ask the operations copilot"
        />
        <button type="button" onClick={onAsk} disabled={loading || question.trim().length === 0} title="Ask copilot">
          <Send size={18} aria-hidden="true" />
        </button>
      </div>
    </section>
  );
}
