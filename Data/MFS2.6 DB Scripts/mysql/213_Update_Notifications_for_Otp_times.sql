Update notification set Text="Dear customer , please try to generate new otp after $(RemainingBlockTimeMinutes) minutes." where language =0 and code=2113;
Update notification set Text="Your account has been blocked.Please try again after $(RemainingBlockTimeHours) hours $(RemainingBlockTimeMinutes) minutes." where language =0 and code=2114;
