import { useEffect, useMemo, useState } from "react";
import { api } from "./api";
import { ShowManager } from "./components/ShowManager";
import { WorkoutManager } from "./components/WorkoutManager";
import type { Category, Show, Workout } from "./types";

type ActiveView = "shows" | "workouts";
type LoadState = "idle" | "loading" | "success" | "error";

function extractErrorMessage(error: unknown): string {
  if (error instanceof Error) {
    return error.message;
  }

  return "Something went wrong. Please try again.";
}

export default function App() {
  const [activeView, setActiveView] = useState<ActiveView>("shows");
  const [categories, setCategories] = useState<Category[]>([]);
  const [shows, setShows] = useState<Show[]>([]);
  const [workouts, setWorkouts] = useState<Workout[]>([]);
  const [loadState, setLoadState] = useState<LoadState>("idle");
  const [pageError, setPageError] = useState("");

  useEffect(() => {
    void loadDashboardData();
  }, []);

  async function loadDashboardData() {
    setLoadState("loading");
    setPageError("");

    try {
      const [loadedCategories, loadedShows, loadedWorkouts] = await Promise.all([
        api.getCategories(),
        api.getShows(),
        api.getWorkouts()
      ]);

      setCategories(loadedCategories);
      setShows(loadedShows);
      setWorkouts(loadedWorkouts);
      setLoadState("success");
    } catch (error) {
      setPageError(extractErrorMessage(error));
      setLoadState("error");
    }
  }

  const dashboardCards = useMemo(
    () => [
      {
        title: "Categories",
        value: String(categories.length),
        description: "Categories feed the admin dropdowns used by shows and workouts."
      },
      {
        title: "Shows",
        value: String(shows.length),
        description: "Show records now load from the backend instead of hard-coded mock data."
      },
      {
        title: "Workouts",
        value: String(workouts.length),
        description: "Workout records include trainer, difficulty, tags, and category data."
      }
    ],
    [categories.length, shows.length, workouts.length]
  );

  return (
    <div className="min-h-screen bg-slate-100 text-slate-900">
      <header className="border-b border-slate-200 bg-white">
        <div className="mx-auto flex max-w-7xl flex-col gap-4 px-6 py-5 lg:flex-row lg:items-center lg:justify-between">
          <div>
            <p className="text-sm font-medium uppercase tracking-[0.3em] text-orange-600">
              Internal Admin
            </p>
            <h1 className="mt-1 text-2xl font-bold">Editorial Content Management Platform</h1>
            <p className="mt-2 max-w-3xl text-sm leading-6 text-slate-600">
              Phase 3 connects the React admin app to live Spring Boot APIs so editors can create,
              update, and delete content from one screen.
            </p>
          </div>
          <button
            type="button"
            onClick={() => void loadDashboardData()}
            className="rounded-full bg-brand-dark px-5 py-3 text-sm font-semibold text-white transition hover:bg-slate-800"
          >
            Refresh Data
          </button>
        </div>
      </header>

      <main className="mx-auto max-w-7xl px-6 py-8">
        <section className="grid gap-6 md:grid-cols-3">
          {dashboardCards.map((card) => (
            <article
              key={card.title}
              className="rounded-3xl bg-white p-6 shadow-sm ring-1 ring-slate-200"
            >
              <p className="text-sm font-medium uppercase tracking-[0.25em] text-slate-500">
                {card.title}
              </p>
              <p className="mt-4 text-4xl font-bold">{card.value}</p>
              <p className="mt-3 text-sm leading-6 text-slate-600">{card.description}</p>
            </article>
          ))}
        </section>

        <section className="mt-8 rounded-3xl bg-white p-6 shadow-sm ring-1 ring-slate-200">
          <div className="flex flex-wrap items-center gap-3">
            <button
              type="button"
              onClick={() => setActiveView("shows")}
              className={`rounded-full px-4 py-2 text-sm font-semibold transition ${
                activeView === "shows"
                  ? "bg-orange-500 text-white"
                  : "bg-slate-100 text-slate-700 hover:bg-slate-200"
              }`}
            >
              Manage Shows
            </button>
            <button
              type="button"
              onClick={() => setActiveView("workouts")}
              className={`rounded-full px-4 py-2 text-sm font-semibold transition ${
                activeView === "workouts"
                  ? "bg-orange-500 text-white"
                  : "bg-slate-100 text-slate-700 hover:bg-slate-200"
              }`}
            >
              Manage Workouts
            </button>
            <span className="ml-auto text-sm text-slate-500">
              API base URL: <code className="rounded bg-slate-100 px-2 py-1">http://localhost:8080/api/v1</code>
            </span>
          </div>

          {loadState === "loading" && (
            <div className="mt-6 rounded-2xl bg-blue-50 px-4 py-3 text-sm text-blue-700">
              Loading categories, shows, and workouts from the backend...
            </div>
          )}

          {pageError && (
            <div className="mt-6 rounded-2xl bg-red-50 px-4 py-3 text-sm text-red-700">
              Failed to load data: {pageError}
            </div>
          )}

          <div className="mt-8">
            {activeView === "shows" ? (
              <ShowManager categories={categories} shows={shows} setShows={setShows} />
            ) : (
              <WorkoutManager categories={categories} workouts={workouts} setWorkouts={setWorkouts} />
            )}
          </div>
        </section>
      </main>
    </div>
  );
}
