const API_BASE = import.meta.env.VITE_API_BASE_URL ?? '';

export function resolveImageUrl(path: string | null | undefined): string {
  if (!path) return '';
  if (path.startsWith('http://') || path.startsWith('https://')) return path;
  return API_BASE + path;
}
