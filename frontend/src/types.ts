export type PublishingStatus = "DRAFT" | "REVIEW" | "PUBLISHED";

export type WorkoutDifficulty = "BEGINNER" | "INTERMEDIATE" | "ADVANCED";

export interface Category {
  id: number;
  name: string;
  description: string | null;
}

export interface Show {
  id: number;
  title: string;
  description: string;
  categoryId: number;
  categoryName: string;
  status: PublishingStatus;
  published: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Workout {
  id: number;
  title: string;
  description: string;
  trainerName: string;
  durationMinutes: number;
  difficulty: WorkoutDifficulty;
  categoryId: number;
  categoryName: string;
  status: PublishingStatus;
  tags: string[];
  createdAt: string;
  updatedAt: string;
}

export interface ShowPayload {
  title: string;
  description: string;
  categoryId: number;
}

export interface WorkoutPayload {
  title: string;
  description: string;
  trainerName: string;
  durationMinutes: number;
  difficulty: WorkoutDifficulty;
  categoryId: number;
  tags: string[];
}

export interface AuditLog {
  id: number;
  entityType: "SHOW" | "WORKOUT";
  entityId: number;
  action: "CREATED" | "UPDATED" | "DELETED" | "STATUS_CHANGED";
  oldStatus: string | null;
  newStatus: string | null;
  actorName: string;
  message: string;
  createdAt: string;
}

export interface SearchResult {
  id: string;
  contentType: string;
  contentId: number;
  title: string;
  description: string;
  categoryName: string;
  trainerName: string | null;
  status: string;
  tags: string[];
}
