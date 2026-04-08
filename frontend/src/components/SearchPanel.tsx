import { useState } from "react";
import { api } from "../api";
import type { Category, SearchResult } from "../types";

interface SearchPanelProps {
  categories: Category[];
}

export function SearchPanel({ categories }: SearchPanelProps) {
  const [query, setQuery] = useState("");
  const [category, setCategory] = useState("");
  const [trainer, setTrainer] = useState("");
  const [tag, setTag] = useState("");
  const [results, setResults] = useState<SearchResult[]>([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  async function handleSearch(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setLoading(true);
    setMessage("");

    try {
      const searchResults = await api.searchContent({
        q: query,
        category,
        trainer,
        tag
      });
      setResults(searchResults);
      setMessage(`Found ${searchResults.length} result(s).`);
    } catch (error) {
      setMessage(error instanceof Error ? error.message : "Search failed.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="rounded-3xl bg-white p-6 shadow-sm ring-1 ring-slate-200">
      <div>
        <h2 className="text-xl font-semibold">Search Content</h2>
        <p className="mt-2 text-sm leading-6 text-slate-600">
          This panel sends filters to the backend search API, which queries Solr.
        </p>
      </div>

      <form onSubmit={handleSearch} className="mt-6 grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        <input
          value={query}
          onChange={(event) => setQuery(event.target.value)}
          placeholder="Search by title"
          className="rounded-2xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-orange-400 focus:ring-2 focus:ring-orange-200"
        />
        <select
          value={category}
          onChange={(event) => setCategory(event.target.value)}
          className="rounded-2xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-orange-400 focus:ring-2 focus:ring-orange-200"
        >
          <option value="">All categories</option>
          {categories.map((item) => (
            <option key={item.id} value={item.name}>
              {item.name}
            </option>
          ))}
        </select>
        <input
          value={trainer}
          onChange={(event) => setTrainer(event.target.value)}
          placeholder="Trainer name"
          className="rounded-2xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-orange-400 focus:ring-2 focus:ring-orange-200"
        />
        <input
          value={tag}
          onChange={(event) => setTag(event.target.value)}
          placeholder="Tag"
          className="rounded-2xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-orange-400 focus:ring-2 focus:ring-orange-200"
        />

        <button
          type="submit"
          disabled={loading}
          className="rounded-2xl bg-brand-dark px-4 py-3 text-sm font-semibold text-white transition hover:bg-slate-800 disabled:opacity-60 md:col-span-2 xl:col-span-4"
        >
          {loading ? "Searching..." : "Search Solr"}
        </button>
      </form>

      {message && (
        <div className="mt-4 rounded-2xl bg-slate-50 px-4 py-3 text-sm text-slate-700">{message}</div>
      )}

      <div className="mt-6 space-y-4">
        {results.map((result) => (
          <article key={result.id} className="rounded-2xl border border-slate-200 bg-slate-50 p-4">
            <div className="flex flex-wrap items-center gap-2">
              <span className="rounded-full bg-orange-100 px-3 py-1 text-xs font-semibold text-orange-700">
                {result.contentType}
              </span>
              <span className="rounded-full bg-slate-200 px-3 py-1 text-xs font-semibold text-slate-700">
                {result.status}
              </span>
            </div>
            <h3 className="mt-3 text-lg font-semibold text-slate-900">{result.title}</h3>
            <p className="mt-2 text-sm text-slate-600">{result.description}</p>
            <p className="mt-2 text-xs text-slate-500">
              Category: {result.categoryName}
              {result.trainerName ? ` | Trainer: ${result.trainerName}` : ""}
            </p>
            {result.tags.length > 0 && (
              <div className="mt-3 flex flex-wrap gap-2">
                {result.tags.map((item) => (
                  <span
                    key={`${result.id}-${item}`}
                    className="rounded-full bg-blue-100 px-3 py-1 text-xs font-semibold text-blue-700"
                  >
                    {item}
                  </span>
                ))}
              </div>
            )}
          </article>
        ))}
        {results.length === 0 && (
          <div className="rounded-2xl border border-dashed border-slate-300 px-4 py-8 text-center text-sm text-slate-500">
            Search results will appear here.
          </div>
        )}
      </div>
    </section>
  );
}
