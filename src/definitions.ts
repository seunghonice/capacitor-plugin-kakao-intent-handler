declare module '@capacitor/core' {
  interface PluginRegistry {
    KakaoIntentHandler: KakaoIntentHandlerPlugin;
  }
}

export interface KakaoIntentHandlerPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
