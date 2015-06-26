n = 24*6;

% North America
n_america = zeros(n,1);
t_0010=1;
t_0300=3*6;
n_america(t_0010:t_0300) = 16*10^6;
t_0830=8*6+3;
n_america(t_0300:t_0830) = linspace(16*10^6, 5.8*10^6, t_0830-t_0300+1);
t_1500=15*6;
n_america(t_0830:t_1500) = linspace(5.8*10^6, 16*10^6, t_1500-t_0830+1);
t_2400=24*6;
n_america(t_1500:t_2400) = 16*10^6;

% South America
s_america = zeros(n,1);
t_0030=3;
t_0130=1*6+3;
s_america(t_0030:t_0130) = 3.3*10^6;
t_0800=8*6;
s_america(t_0130:t_0800) = linspace(3.3*10^6, 0.21*10^6, t_0800-t_0130+1);
t_1430=14*6+3;
s_america(t_0800:t_1430) = linspace(0.21*10^6, 2.5*10^6, t_1430-t_0800+1);
t_2100=21*6;
s_america(t_1430:t_2100) = 2.5*10^6;
aux=linspace(2.5*10^6, 3.3*10^6, t_2400-t_2100+t_0030+1);
s_america(t_2100:t_2400) = aux(1:t_2400-t_2100+1);
s_america(t_0010:t_0030) = aux(t_2400-t_2100+1:t_2400-t_2100+t_0030-t_0010+1);

x = [1:n];

plot(x,n_america);
plot(x,s_america);