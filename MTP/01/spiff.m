[vec_ana_x, vec_ana_y] = mtp0101_ana_iterate(0.001, 0.2);
[vec_ode45_x, vec_ode45_y] = ode1(@mtp0101, [0,0.2], [1]);
[vec_eulerexpl_x, vec_eulerexpl_y] = euler_expl(0.003, 0.2);

hold on;

plot(vec_ana_x, vec_ana_y, 'r');
title('Approximation');
plot(vec_eulerexpl_x, vec_eulerexpl_y, 'k');
plot(vec_ode45_x, vec_ode45_y,'g');
grid on;

figure;
hold on;
title('Error');
plot(vec_ana_x, mtp0101_ana_fromXVec(vec_ana_x)-vec_ana_y, 'r');
plot(vec_eulerexpl_x, mtp0101_ana_fromXVec(vec_eulerexpl_x)-vec_eulerexpl_y, 'k');
plot(vec_ode45_x, mtp0101_ana_fromXVec(vec_ode45_x)-vec_ode45_y, 'g');
grid on;
