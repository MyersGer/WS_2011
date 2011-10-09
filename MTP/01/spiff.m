function spiff(h)

[vec_ana_x, vec_ana_y] = mtp0101_ana_iterate(h, 0.2);
[vec_ode45_x, vec_ode45_y] = ode45(@mtp0101, [0,0.2], [1]);
[vec_eulerexpl_x, vec_eulerexpl_y] = euler_expl(h, 0.2, @mtp0101);
[vec_rk2_x, vec_rk2_y] = rk2(h, 0.2, @mtp0101);


hold on;

plot(vec_ana_x, vec_ana_y, 'r');
plot(vec_eulerexpl_x, vec_eulerexpl_y, 'k');
plot(vec_ode45_x, vec_ode45_y,'g');
plot(vec_rk2_x, vec_rk2_y,'m');
grid on;
title('Approximation');
legend('analytische Lösung', 'Vorwärts Euler','Rückwärts Euler', 'Runge-Kutta');



figure;

hold on;

plot(vec_ana_x, mtp0101_ana_fromXVec(vec_ana_x)-vec_ana_y, 'r');
plot(vec_eulerexpl_x, mtp0101_ana_fromXVec(vec_eulerexpl_x)-vec_eulerexpl_y, 'k');
plot(vec_ode45_x, mtp0101_ana_fromXVec(vec_ode45_x)-vec_ode45_y, 'g');
plot(vec_rk2_x, mtp0101_ana_fromXVec(vec_rk2_x)-vec_rk2_y, 'm');
grid on;
title('Error');
legend('analytische Lösung', 'Vorwärts Euler','Rückwärts Euler', 'Runge-Kutta');

end

