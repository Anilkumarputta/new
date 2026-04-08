import type { Category, Show, ShowPayload, Workout, WorkoutPayload } from "./types";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080/api/v1";

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...(options?.headers ?? {})
    },
    ...options
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(errorText || `Request failed with status ${response.status}`);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json() as Promise<T>;
}

export const api = {
  getCategories: () => request<Category[]>("/categories"),
  getShows: () => request<Show[]>("/shows"),
  createShow: (payload: ShowPayload) =>
    request<Show>("/shows", {
      method: "POST",
      body: JSON.stringify(payload)
    }),
  updateShow: (id: number, payload: ShowPayload) =>
    request<Show>(`/shows/${id}`, {
      method: "PUT",
      body: JSON.stringify(payload)
    }),
  deleteShow: (id: number) =>
    request<void>(`/shows/${id}`, {
      method: "DELETE"
    }),
  getWorkouts: () => request<Workout[]>("/workouts"),
  createWorkout: (payload: WorkoutPayload) =>
    request<Workout>("/workouts", {
      method: "POST",
      body: JSON.stringify(payload)
    }),
  updateWorkout: (id: number, payload: WorkoutPayload) =>
    request<Workout>(`/workouts/${id}`, {
      method: "PUT",
      body: JSON.stringify(payload)
    }),
  deleteWorkout: (id: number) =>
    request<void>(`/workouts/${id}`, {
      method: "DELETE"
    })
};
