const dashboardCards = [
  {
    title: "Shows",
    value: "24",
    description: "Editorial teams will create and manage show records here."
  },
  {
    title: "Workouts",
    value: "58",
    description: "Workout management will connect trainers, tags, and difficulty."
  },
  {
    title: "Publishing Queue",
    value: "7",
    description: "Draft and review items will move through the publishing workflow."
  }
];

export default function App() {
  return (
    <div className="min-h-screen bg-slate-100 text-slate-900">
      <header className="border-b border-slate-200 bg-white">
        <div className="mx-auto flex max-w-6xl items-center justify-between px-6 py-4">
          <div>
            <p className="text-sm font-medium uppercase tracking-[0.3em] text-orange-600">
              Internal Admin
            </p>
            <h1 className="mt-1 text-2xl font-bold text-slate-900">
              Editorial Content Management Platform
            </h1>
          </div>
          <div className="rounded-full bg-orange-100 px-4 py-2 text-sm font-semibold text-orange-700">
            Mock Admin Login
          </div>
        </div>
      </header>

      <main className="mx-auto max-w-6xl px-6 py-10">
        <section className="grid gap-6 lg:grid-cols-[2fr_1fr]">
          <div className="rounded-3xl bg-brand-dark p-8 text-white shadow-xl">
            <p className="text-sm uppercase tracking-[0.3em] text-orange-300">
              Phase 1 Dashboard Shell
            </p>
            <h2 className="mt-4 max-w-2xl text-4xl font-bold leading-tight">
              One place for editors to manage shows, workouts, episodes, search, and publishing.
            </h2>
            <p className="mt-4 max-w-2xl text-base text-slate-300">
              This screen is simple on purpose. Right now it is a shell. In later phases we will
              connect it to real backend APIs, data, and workflows.
            </p>
          </div>

          <div className="rounded-3xl bg-white p-6 shadow-sm ring-1 ring-slate-200">
            <h3 className="text-lg font-semibold">Backend Connection</h3>
            <p className="mt-3 text-sm leading-6 text-slate-600">
              Backend base URL for local development:
            </p>
            <code className="mt-4 block rounded-xl bg-slate-100 px-4 py-3 text-sm text-slate-800">
              http://localhost:8080/api/v1
            </code>
            <p className="mt-4 text-sm leading-6 text-slate-600">
              First backend endpoint:
            </p>
            <code className="mt-2 block rounded-xl bg-slate-100 px-4 py-3 text-sm text-slate-800">
              GET /health
            </code>
          </div>
        </section>

        <section className="mt-8 grid gap-6 md:grid-cols-3">
          {dashboardCards.map((card) => (
            <article
              key={card.title}
              className="rounded-3xl bg-white p-6 shadow-sm ring-1 ring-slate-200"
            >
              <p className="text-sm font-medium uppercase tracking-[0.25em] text-slate-500">
                {card.title}
              </p>
              <p className="mt-4 text-4xl font-bold text-slate-900">{card.value}</p>
              <p className="mt-3 text-sm leading-6 text-slate-600">{card.description}</p>
            </article>
          ))}
        </section>
      </main>
    </div>
  );
}
