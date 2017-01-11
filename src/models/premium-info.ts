export class PremiumInfo {

  objectName: string;
  objectWorth: string;
  predictedPremium: string;

  constructor(objectName: string, objectWorth: string, predictedPremium: string) {
    this.objectName = objectName;
    this.objectWorth = objectWorth;
    this.predictedPremium = predictedPremium;
  }

}
