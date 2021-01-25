import { WebPlugin } from '@capacitor/core';
import { KakaoIntentHandlerPlugin } from './definitions';

export class KakaoIntentHandlerWeb extends WebPlugin implements KakaoIntentHandlerPlugin {
  constructor() {
    super({
      name: 'KakaoIntentHandler',
      platforms: ['web'],
    });
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}

const KakaoIntentHandler = new KakaoIntentHandlerWeb();

export { KakaoIntentHandler };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(KakaoIntentHandler);
