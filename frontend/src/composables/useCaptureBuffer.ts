import type { CapturedPhoto } from './useCamera';

let _buffer: CapturedPhoto | null = null;

export function useCaptureBuffer() {
  function set(photo: CapturedPhoto) { _buffer = photo; }
  function consume(): CapturedPhoto | null { const p = _buffer; _buffer = null; return p; }
  return { set, consume };
}
