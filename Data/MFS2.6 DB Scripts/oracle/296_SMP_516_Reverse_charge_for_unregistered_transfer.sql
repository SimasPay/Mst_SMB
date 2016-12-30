update system_parameters set parametervalue = 'false' where parametername = 'reverse.charge.for.expired.transfer.to.unregistered';

commit;
