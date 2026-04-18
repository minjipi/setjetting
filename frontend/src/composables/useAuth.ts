import { ref } from 'vue';
import { userApi } from '@/api/userApi';

interface UserInfo {
  idx: number;
  email: string;
  name: string;
  profileImageUrl?: string;
  role: string;
  provider?: string;
}

const STORAGE_KEY = 'sj_user';

const user = ref<UserInfo | null>(
  JSON.parse(localStorage.getItem(STORAGE_KEY) || 'null')
);
const isLoggedIn = ref<boolean>(!!user.value);

export function useAuth() {
  function setUser(data: UserInfo) {
    user.value = data;
    isLoggedIn.value = true;
    localStorage.setItem(STORAGE_KEY, JSON.stringify(data));
  }

  function clearUser() {
    user.value = null;
    isLoggedIn.value = false;
    localStorage.removeItem(STORAGE_KEY);
  }

  async function login(email: string, password: string): Promise<boolean> {
    const data = await userApi.login(email, password);
    if (!data) return false;
    setUser(data);
    return true;
  }

  async function logout(): Promise<void> {
    try {
      await userApi.logout();
    } finally {
      clearUser();
    }
  }

  async function checkAuth(): Promise<boolean> {
    try {
      await userApi.checkAuth();
      return true;
    } catch {
      clearUser();
      return false;
    }
  }

  return { user, isLoggedIn, login, logout, checkAuth, setUser, clearUser };
}
