import type { AuditLog } from "../types";

interface AuditLogPanelProps {
  logs: AuditLog[];
}

function formatDate(value: string): string {
  return new Date(value).toLocaleString();
}

export function AuditLogPanel({ logs }: AuditLogPanelProps) {
  return (
    <section className="rounded-3xl bg-white p-6 shadow-sm ring-1 ring-slate-200">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-xl font-semibold">Activity Log</h2>
          <p className="mt-2 text-sm leading-6 text-slate-600">
            This panel shows the audit trail created by the backend whenever content changes.
          </p>
        </div>
        <span className="rounded-full bg-slate-100 px-3 py-1 text-xs font-semibold text-slate-700">
          {logs.length} events
        </span>
      </div>

      <div className="mt-6 space-y-4">
        {logs.map((log) => (
          <article key={log.id} className="rounded-2xl border border-slate-200 bg-slate-50 p-4">
            <div className="flex flex-wrap items-center gap-2">
              <span className="rounded-full bg-orange-100 px-3 py-1 text-xs font-semibold text-orange-700">
                {log.entityType}
              </span>
              <span className="rounded-full bg-slate-200 px-3 py-1 text-xs font-semibold text-slate-700">
                {log.action}
              </span>
              <span className="text-xs text-slate-500">Entity ID {log.entityId}</span>
            </div>
            <p className="mt-3 text-sm font-semibold text-slate-900">{log.message}</p>
            <p className="mt-2 text-xs text-slate-500">
              Actor: {log.actorName} | {formatDate(log.createdAt)}
            </p>
            {(log.oldStatus || log.newStatus) && (
              <p className="mt-2 text-xs text-slate-500">
                Status: {log.oldStatus ?? "none"} -&gt; {log.newStatus ?? "none"}
              </p>
            )}
          </article>
        ))}
        {logs.length === 0 && (
          <div className="rounded-2xl border border-dashed border-slate-300 px-4 py-8 text-center text-sm text-slate-500">
            No audit activity yet.
          </div>
        )}
      </div>
    </section>
  );
}
