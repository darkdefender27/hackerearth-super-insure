import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';
import { PremiumCalculator } from '../../providers/premium-calculator';

import {Camera} from 'ionic-native';

@Component({
  selector: 'page-premium-info',
  templateUrl: 'premium-info.html'
})
export class PremiumInfoPage {

  public base64Image: string;

  constructor(public navCtrl: NavController, public navParams: NavParams,
              private premiumCalculator: PremiumCalculator) {}

  ionViewDidLoad() {
    console.log('ionViewDidLoad PremiumInfoPage');
  }

  takePicture(){
    //noinspection TypeScriptUnresolvedVariable
    Camera.getPicture({
      destinationType: Camera.DestinationType.DATA_URL,
      targetWidth: 1000,
      targetHeight: 1000
    }).then((imageData) => {
      // imageData is a base64 encoded string
      this.base64Image = "data:image/jpeg;base64," + imageData;
      console.log(this.base64Image);
    }, (err) => {
      console.log(err);
    });
  }

}
