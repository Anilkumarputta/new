import type { Dispatch, FormEvent, SetStateAction } from "react";
import { useEffect, useState } from "react";
import { api } from "../api";
import type { Category, Workout, WorkoutDifficulty, WorkoutPayload } from "../types";

interface WorkoutManagerProps {
  categories: Category[];
  workouts: Workout[];
  setWorkouts: Dispatch<SetStateAction<Workout[]>>;
}

interface WorkoutFormState {
  title: string;
  description: string;
  trainerName: string;
  durationMinutes: string;
  difficulty: WorkoutDifficulty;
  categoryId: string;
  tags: string;
}

const emptyWorkoutForm: WorkoutFormState = {
  title: "",
  description: "",
  trainerName: "",
  durationMinutes: "20",
  difficulty: "BEGINNER",
  categoryId: "",
  tags: ""
};

function extractErrorMessage(error: unknown): string {
  if (error instanceof Error) {
    return error.message;
  }

  return "Something went wrong. Please try again.";
}

function toPayload(form: WorkoutFormState): WorkoutPayload {
  return {
    title: form.title.trim(),
    description: form.description.trim(),
    trainerName: form.trainerName.trim(),
    durationMinutes: Number(form.durationMinutes),
    difficulty: form.difficulty,
    categoryId: Number(form.categoryId),
    tags: form.tags
      .split(",")
      .map((tag) => tag.trim())
      .filter(Boolean)
  };
}

export function WorkoutManager({ categories, workouts, setWorkouts }: WorkoutManagerProps) {
  const [form, setForm] = useState<WorkoutFormState>(emptyWorkoutForm);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [deletingId, setDeletingId] = useState<number | null>(null);
  const [message, setMessage] = useState("");

  useEffect(() => {
    if (!form.categoryId && categories[0]) {
      setForm((current) => ({ ...current, categoryId: String(categories[0].id) }));
    }
  }, [categories, form.categoryId]);

  function resetForm() {
    setEditingId(null);
    setForm({
      ...emptyWorkoutForm,
      categoryId: categories[0] ? String(categories[0].id) : ""
    });
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSubmitting(true);
    setMessage("");

    try {
      const payload = toPayload(form);
      const savedWorkout = editingId
        ? await api.updateWorkout(editingId, payload)
        : await api.createWorkout(payload);

      setWorkouts((current) => {
        if (editingId) {
          return current.map((workout) => (workout.id === savedWorkout.id ? savedWorkout : workout));
        }

        return [savedWorkout, ...current];
      });

      setMessage(editingId ? "Workout updated successfully." : "Workout created successfully.");
      resetForm();
    } catch (error) {
      setMessage(extractErrorMessage(error));
    } finally {
      setSubmitting(false);
    }
  }

  async function handleDelete(id: number) {
    setDeletingId(id);
    setMessage("");

    try {
      await api.deleteWorkout(id);
      setWorkouts((current) => current.filter((workout) => workout.id !== id));
      if (editingId === id) {
        resetForm();
      }
      setMessage("Workout deleted successfully.");
    } catch (error) {
      setMessage(extractErrorMessage(error));
    } finally {
      setDeletingId(null);
    }
  }

  function startEdit(workout: Workout) {
    setEditingId(workout.id);
    setMessage("");
    setForm({
      title: workout.title,
      description: workout.description,
      trainerName: workout.trainerName,
      durationMinutes: String(workout.durationMinutes),
      difficulty: workout.difficulty,
      categoryId: String(workout.categoryId),
      tags: workout.tags.join(", ")
    });
  }

  return (
    <div className="grid gap-8 xl:grid-cols-[420px_1fr]">
      <form onSubmit={handleSubmit} className="rounded-3xl bg-slate-50 p-6 ring-1 ring-slate-200">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-xl font-semibold">
              {editingId ? "Edit Workout" : "Create Workout"}
            </h2>
            <p className="mt-2 text-sm leading-6 text-slate-600">
              Workouts show how the frontend sends numbers, enums, and tag arrays.
            </p>
          </div>
          {editingId && (
            <button
              type="button"
              onClick={resetForm}
              className="text-sm font-semibold text-slate-600 hover:text-slate-900"
            >
              Cancel Edit
            </button>
          )}
        </div>

        {message && (
          <div className="mt-4 rounded-2xl bg-emerald-50 px-4 py-3 text-sm text-emerald-700">
            {message}
          </div>
        )}

        <div className="mt-6 space-y-4">
          <label className="block">
            <span className="text-sm font-semibold text-slate-700">Title</span>
            <input
              value={form.title}
              onChange={(event) => setForm((current) => ({ ...current, title: event.target.value }))}
              className="mt-2 w-full rounded-2xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none transition focus:border-orange-400 focus:ring-2 focus:ring-orange-200"
              required
            />
          </label>

          <label className="block">
            <span className="text-sm font-semibold text-slate-700">Description</span>
            <textarea
              value={form.description}
              onChange={(event) =>
                setForm((current) => ({ ...current, description: event.target.value }))
              }
              className="mt-2 min-h-24 w-full rounded-2xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none transition focus:border-orange-400 focus:ring-2 focus:ring-orange-200"
              required
            />
          </label>

          <label className="block">
            <span className="text-sm font-semibold text-slate-700">Trainer Name</span>
            <input
              value={form.trainerName}
              onChange={(event) =>
                setForm((current) => ({ ...current, trainerName: event.target.value }))
              }
              className="mt-2 w-full rounded-2xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none transition focus:border-orange-400 focus:ring-2 focus:ring-orange-200"
              required
            />
          </label>

          <div className="grid gap-4 md:grid-cols-2">
            <label className="block">
              <span className="text-sm font-semibold text-slate-700">Duration (minutes)</span>
              <input
                type="number"
                min={5}
                max={240}
                value={form.durationMinutes}
                onChange={(event) =>
                  setForm((current) => ({ ...current, durationMinutes: event.target.value }))
                }
                className="mt-2 w-full rounded-2xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none transition focus:border-orange-400 focus:ring-2 focus:ring-orange-200"
                required
              />
            </label>

            <label className="block">
              <span className="text-sm font-semibold text-slate-700">Difficulty</span>
              <select
                value={form.difficulty}
                onChange={(event) =>
                  setForm((current) => ({
                    ...current,
                    difficulty: event.target.value as WorkoutDifficulty
                  }))
                }
                className="mt-2 w-full rounded-2xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none transition focus:border-orange-400 focus:ring-2 focus:ring-orange-200"
              >
                <option value="BEGINNER">BEGINNER</option>
                <option value="INTERMEDIATE">INTERMEDIATE</option>
                <option value="ADVANCED">ADVANCED</option>
              </select>
            </label>
          </div>

          <label className="block">
            <span className="text-sm font-semibold text-slate-700">Category</span>
            <select
              value={form.categoryId}
              onChange={(event) =>
                setForm((current) => ({ ...current, categoryId: event.target.value }))
              }
              className="mt-2 w-full rounded-2xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none transition focus:border-orange-400 focus:ring-2 focus:ring-orange-200"
              required
            >
              <option value="">Select a category</option>
              {categories.map((category) => (
                <option key={category.id} value={category.id}>
                  {category.name}
                </option>
              ))}
            </select>
          </label>

          <label className="block">
            <span className="text-sm font-semibold text-slate-700">Tags</span>
            <input
              value={form.tags}
              onChange={(event) => setForm((current) => ({ ...current, tags: event.target.value }))}
              className="mt-2 w-full rounded-2xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none transition focus:border-orange-400 focus:ring-2 focus:ring-orange-200"
              placeholder="Example: strength, legs, quick"
              required
            />
          </label>
        </div>

        <button
          type="submit"
          disabled={submitting || categories.length === 0}
          className="mt-6 w-full rounded-2xl bg-orange-500 px-4 py-3 text-sm font-semibold text-white transition hover:bg-orange-600 disabled:cursor-not-allowed disabled:bg-orange-300"
        >
          {submitting ? "Saving..." : editingId ? "Update Workout" : "Create Workout"}
        </button>
      </form>

      <section className="overflow-hidden rounded-3xl bg-white ring-1 ring-slate-200">
        <div className="border-b border-slate-200 px-6 py-5">
          <h2 className="text-xl font-semibold">Workouts Table</h2>
          <p className="mt-2 text-sm leading-6 text-slate-600">
            Tags, trainer, and difficulty all come from the backend response.
          </p>
        </div>
        <div className="overflow-x-auto">
          <table className="min-w-full text-left text-sm">
            <thead className="bg-slate-50 text-slate-600">
              <tr>
                <th className="px-6 py-4 font-semibold">Workout</th>
                <th className="px-6 py-4 font-semibold">Trainer</th>
                <th className="px-6 py-4 font-semibold">Details</th>
                <th className="px-6 py-4 font-semibold">Tags</th>
                <th className="px-6 py-4 font-semibold">Actions</th>
              </tr>
            </thead>
            <tbody>
              {workouts.map((workout) => (
                <tr key={workout.id} className="border-t border-slate-200">
                  <td className="px-6 py-4">
                    <p className="font-semibold text-slate-900">{workout.title}</p>
                    <p className="mt-1 max-w-md text-xs leading-5 text-slate-500">
                      {workout.description}
                    </p>
                  </td>
                  <td className="px-6 py-4">
                    <p className="font-semibold">{workout.trainerName}</p>
                    <p className="text-xs text-slate-500">{workout.categoryName}</p>
                  </td>
                  <td className="px-6 py-4 text-slate-600">
                    <p>{workout.durationMinutes} min</p>
                    <p className="mt-1 text-xs">{workout.difficulty}</p>
                    <p className="mt-1 text-xs">{workout.status}</p>
                  </td>
                  <td className="px-6 py-4">
                    <div className="flex max-w-xs flex-wrap gap-2">
                      {workout.tags.map((tag) => (
                        <span
                          key={`${workout.id}-${tag}`}
                          className="rounded-full bg-orange-100 px-3 py-1 text-xs font-semibold text-orange-700"
                        >
                          {tag}
                        </span>
                      ))}
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <div className="flex gap-3">
                      <button
                        type="button"
                        onClick={() => startEdit(workout)}
                        className="text-sm font-semibold text-orange-600 hover:text-orange-700"
                      >
                        Edit
                      </button>
                      <button
                        type="button"
                        onClick={() => void handleDelete(workout.id)}
                        disabled={deletingId === workout.id}
                        className="text-sm font-semibold text-red-600 hover:text-red-700 disabled:text-red-300"
                      >
                        {deletingId === workout.id ? "Deleting..." : "Delete"}
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
              {workouts.length === 0 && (
                <tr>
                  <td colSpan={5} className="px-6 py-10 text-center text-slate-500">
                    No workouts found yet.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  );
}
