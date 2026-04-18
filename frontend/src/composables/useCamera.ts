import { Capacitor } from '@capacitor/core';
import { Camera, CameraResultType, CameraSource } from '@capacitor/camera';

export interface CapturedPhoto {
  dataUrl: string;
  format: string;
}

export function useCamera() {
  const isNative = Capacitor.isNativePlatform();

  /** 네이티브(iOS/Android): Capacitor Camera 플러그인 사용 */
  async function captureNative(): Promise<CapturedPhoto> {
    const photo = await Camera.getPhoto({
      quality: 90,
      allowEditing: false,
      resultType: CameraResultType.DataUrl,
      source: CameraSource.Camera,
    });
    return {
      dataUrl: photo.dataUrl!,
      format: photo.format,
    };
  }

  /** 웹: canvas로 videoElement 프레임 캡처 */
  function captureFromVideo(videoEl: HTMLVideoElement): CapturedPhoto {
    const canvas = document.createElement('canvas');
    canvas.width = videoEl.videoWidth;
    canvas.height = videoEl.videoHeight;
    canvas.getContext('2d')!.drawImage(videoEl, 0, 0);
    return {
      dataUrl: canvas.toDataURL('image/jpeg', 0.9),
      format: 'jpeg',
    };
  }

  /** 웹: getUserMedia 스트림 시작 */
  async function startStream(videoEl: HTMLVideoElement): Promise<MediaStream> {
    const stream = await navigator.mediaDevices.getUserMedia({
      video: { facingMode: 'environment', width: { ideal: 1280 }, height: { ideal: 720 } },
    });
    videoEl.srcObject = stream;
    await videoEl.play();
    return stream;
  }

  /** 스트림 종료 */
  function stopStream(stream: MediaStream | null) {
    stream?.getTracks().forEach(t => t.stop());
  }

  return { isNative, captureNative, captureFromVideo, startStream, stopStream };
}
