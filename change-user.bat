set person=%1

@ if !%person%! == !colin! goto :colin
@ if !%person%! == !noah! goto :noah
@ if !%person%! == !julian! goto :julian
@ if !%person%! == !aidan! goto :aidan

@echo Unknown person: %person%
@goto :finish

:colin
@echo Setting user to 'colin' 
@git config user.name "Colin Voorhis"
@git config user.email "21cvoorhis@gmail.com"
@goto :finish

:noah
@echo Setting user to 'noah' 
@git config user.name "Noah Charlton"
@git config user.email "ncharlton002@gmail.com"
@goto :finish

:julian
@echo Setup a github account!
@goto :finish

:aidan
@echo Setup a github account!
@goto :finish

:finish
